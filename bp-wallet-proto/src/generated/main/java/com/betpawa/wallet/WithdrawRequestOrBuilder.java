// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: helloworld.proto

package com.betpawa.wallet;

public interface WithdrawRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:wallet.WithdrawRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 userID = 1;</code>
   */
  int getUserID();

  /**
   * <code>float amount = 2;</code>
   */
  float getAmount();

  /**
   * <code>.wallet.CURRENCY currency = 3;</code>
   */
  int getCurrencyValue();
  /**
   * <code>.wallet.CURRENCY currency = 3;</code>
   */
  com.betpawa.wallet.CURRENCY getCurrency();
}