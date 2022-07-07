package com.exrade.models.userprofile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IntervalUnit {

    DAY("DAY"), WEEK("WEEK"), MONTH("MONTH"), YEAR("YEAR");

    private String value;

    private IntervalUnit( final String value ) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return this.value;
    }

    @JsonCreator
    public static IntervalUnit create( final String value ) {
      for( IntervalUnit unit : IntervalUnit.values() ) {
        if( unit.getValue().equals( value.toUpperCase() ) ) {
          return unit;
        }
      }
      throw new IllegalArgumentException( "Invalid value for IntervalUnit:" + value );
    }

    @Override
    public String toString() {
      return this.value;
    }
  }