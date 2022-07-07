package com.exrade.runtime.userprofile.providers.local;

import com.exrade.core.ExLogger;
import com.exrade.models.userprofile.Profile;
import com.exrade.runtime.conf.ExConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.web3j.crypto.*;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

import static org.web3j.crypto.TransactionEncoder.asRlpValues;


public class LocalWalletProvider {

    private static final String WALLET_FILE_DESTINATION = ExConfiguration.getStringProperty("wallet-file-destination");
    private static final String WALLET_FILE_PASSWORD = ExConfiguration.getStringProperty("wallet-file-password");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public LocalWalletProvider() {

    }

    public Credentials generateKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        WalletFile wallet = Wallet.createStandard(WALLET_FILE_PASSWORD, keyPair);
        return Credentials.create(keyPair.getPrivateKey().toString(16), wallet.getAddress());
    }

    public Credentials getWalletCredential(Profile profile) throws CipherException, IOException {
    	String walletFile = getWalletFileDestinationDir() + File.separator + profile.getWalletAddress() + ".json";
    	ExLogger.get().debug("Loading credentials from wallet file: {}", walletFile);
        return WalletUtils.loadCredentials(WALLET_FILE_PASSWORD, walletFile);
    }

    public String generateWalletFile() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        return WalletUtils.generateNewWalletFile(WALLET_FILE_PASSWORD, new File(getWalletFileDestinationDir()));
    }

    public String getWalletAddress() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        return generateWalletAddress(WALLET_FILE_PASSWORD, true);
    }

    public String generateWalletAddress(String password, boolean useFullScrypt) throws CipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(password, keyPair);
        }
        else {
            walletFile = Wallet.createLight(password, keyPair);
        }
        String fileName = Keys.toChecksumAddress(walletFile.getAddress());
        File destination = new File(new File(getWalletFileDestinationDir()), fileName + ".json");
        objectMapper.writeValue(destination, walletFile);
        return fileName;
    }

    public String signedDataWithRawTransaction(Profile profile, RawTransaction rawTransaction) throws CipherException, IOException {
    	ExLogger.get().debug("Signing transaction. Wallet: {}, Nonce: {}, GasPrice: {}, GasLimit: {}, To: {}, Data: {}", 
    			profile.getWalletAddress(), rawTransaction.getNonce(), rawTransaction.getGasPrice(), rawTransaction.getGasLimit(), rawTransaction.getTo(), rawTransaction.getData());
    	
    	Credentials credentials = getWalletCredential(profile);
    	
    	ExLogger.get().debug("Loaded credentials. Address: {}, PublicKey: {}", credentials.getAddress(), credentials.getEcKeyPair().getPublicKey());
    	
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        String signedMessageStr = Numeric.toHexString(signedMessage);
        ExLogger.get().debug("Signed message: {}", signedMessageStr);
        return signedMessageStr;
    }


    public RawTransaction getRawTransaction() {
        return RawTransaction.createTransaction(null, null, null, null, "data");
    }

    public byte[] encodedDataWithRawTransaction(byte[] encodedTransaction) throws NoSuchAlgorithmException, CipherException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Sign.SignatureData signatureData = Sign.signMessage(encodedTransaction, generateKeyPair().getEcKeyPair());
        return encode(getRawTransaction(), signatureData);
    }

    public byte[] encode(RawTransaction rawTransaction) {
        return encode(rawTransaction, (Sign.SignatureData) null);
    }

    private byte[] encode(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> values = asRlpValues(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    private String getWalletFileDestinationDir() {
    	if(!Strings.isNullOrEmpty(WALLET_FILE_DESTINATION)) {
    		return WALLET_FILE_DESTINATION;
    	}
    	else {
    		try {
    			Path path = Files.createDirectories(Paths.get(System.getProperty("user.home"), ".localwallet"));
    			return path.normalize().toString();
			} catch (IOException e) {
				return "";
			}
    	}
    }
}
