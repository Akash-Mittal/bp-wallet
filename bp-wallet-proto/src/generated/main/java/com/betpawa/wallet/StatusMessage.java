// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: helloworld.proto

package com.betpawa.wallet;

/**
 * Protobuf enum {@code wallet.StatusMessage}
 */
public enum StatusMessage
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>USER_DOES_NOT_EXIST = 0;</code>
   */
  USER_DOES_NOT_EXIST(0),
  /**
   * <code>INVALID_ARGUMENTS = 1;</code>
   */
  INVALID_ARGUMENTS(1),
  /**
   * <code>INSUFFICIENT_BALANCE = 2;</code>
   */
  INSUFFICIENT_BALANCE(2),
  /**
   * <code>AMOUNT_SHOULD_BE_GREATER_THAN_ZERO = 3;</code>
   */
  AMOUNT_SHOULD_BE_GREATER_THAN_ZERO(3),
  /**
   * <code>INVALID_CURRENCY = 4;</code>
   */
  INVALID_CURRENCY(4),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>USER_DOES_NOT_EXIST = 0;</code>
   */
  public static final int USER_DOES_NOT_EXIST_VALUE = 0;
  /**
   * <code>INVALID_ARGUMENTS = 1;</code>
   */
  public static final int INVALID_ARGUMENTS_VALUE = 1;
  /**
   * <code>INSUFFICIENT_BALANCE = 2;</code>
   */
  public static final int INSUFFICIENT_BALANCE_VALUE = 2;
  /**
   * <code>AMOUNT_SHOULD_BE_GREATER_THAN_ZERO = 3;</code>
   */
  public static final int AMOUNT_SHOULD_BE_GREATER_THAN_ZERO_VALUE = 3;
  /**
   * <code>INVALID_CURRENCY = 4;</code>
   */
  public static final int INVALID_CURRENCY_VALUE = 4;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static StatusMessage valueOf(int value) {
    return forNumber(value);
  }

  public static StatusMessage forNumber(int value) {
    switch (value) {
      case 0: return USER_DOES_NOT_EXIST;
      case 1: return INVALID_ARGUMENTS;
      case 2: return INSUFFICIENT_BALANCE;
      case 3: return AMOUNT_SHOULD_BE_GREATER_THAN_ZERO;
      case 4: return INVALID_CURRENCY;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<StatusMessage>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      StatusMessage> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<StatusMessage>() {
          public StatusMessage findValueByNumber(int number) {
            return StatusMessage.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return com.betpawa.wallet.WalletClass.getDescriptor().getEnumTypes().get(1);
  }

  private static final StatusMessage[] VALUES = values();

  public static StatusMessage valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private StatusMessage(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:wallet.StatusMessage)
}
