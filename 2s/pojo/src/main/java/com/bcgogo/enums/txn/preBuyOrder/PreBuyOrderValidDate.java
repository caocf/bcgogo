package com.bcgogo.enums.txn.preBuyOrder;

public enum PreBuyOrderValidDate {
  ONE_DAY("1天内有效",1,"ONE_DAY"),
  SECOND_DAY("2天内有效",2,"SECOND_DAY"),
  THREE_DAY("3天内有效",3,"THREE_DAY"),
  SEVEN_DAY("7天内有效",7,"SEVEN_DAY"),
  FIFTEEN_DAY("15天内有效",15,"FIFTEEN_DAY"),
  THIRTY_DAY("30天内有效",30,"THIRTY_DAY");

  private final String name;
  private final Integer value;
  private final String label;

  private PreBuyOrderValidDate(String name,Integer value,String label) {
    this.name = name;
    this.value = value;
      this.label=label;
  }

  public String getName() {
    return name;
  }
  public Integer getValue() {
    return value;
  }

    public String getLabel() {
        return label;
    }
}
