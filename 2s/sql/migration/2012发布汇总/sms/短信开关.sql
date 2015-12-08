update notification.message_template set notification.message_template.name="完工提醒",notification.message_template.scene="FINISH_MSG",necessary="UNNECESSARY" where notification.message_template.type="sendFinishMsg1" or notification.message_template.type="sendFinishMsg2";

update notification.message_template set notification.message_template.name="欠款备忘",notification.message_template.scene="BOSS_DEBT_MSG",necessary="UNNECESSARY" where notification.message_template.type="sendDebtMsg1" or notification.message_template.type="sendDebtMsg2" or notification.message_template.type="sendDebtMsg3"
 or notification.message_template.type="sendDebtMsg4" or notification.message_template.type="sendDebtMsg5";

update notification.message_template set notification.message_template.name="消费欠款提醒",notification.message_template.scene="CUSTOMER_DEBT_MSG",necessary="NECESSARY" where notification.message_template.type="customerRemindDebt";

update notification.message_template set notification.message_template.name="折扣备忘",notification.message_template.scene="DISCOUNT_MSG",necessary="UNNECESSARY" where notification.message_template.type="sendDiscountMsg1" or notification.message_template.type="sendDiscountMsg2" or notification.message_template.type="sendDiscountMsg3"
 or notification.message_template.type="sendDiscountMsg4";

update notification.message_template set notification.message_template.name="保险到期提醒",notification.message_template.scene="CUSTOMER_REMIND_GUARANTEE",necessary="NECESSARY" where notification.message_template.type="customerRemindGuarantee";

update notification.message_template set notification.message_template.name="验车到期提醒",notification.message_template.scene="CUSTOMER_REMIND_VALIDATE_CAR",necessary="NECESSARY" where notification.message_template.type="customerRemindValidateCar";

update notification.message_template set notification.message_template.name="生日提醒",notification.message_template.scene="CUSTOMER_REMIND_BIRTHDAY",necessary="NECESSARY" where notification.message_template.type="customerRemindBirthday";

update notification.message_template set notification.message_template.name="保养到期提醒",notification.message_template.scene="CUSTOMER_REMIND_KEEP_IN_GOOD_REPAIR",necessary="NECESSARY" where notification.message_template.type="customerRemindKeepInGoodRepair";

update notification.message_template set notification.message_template.name="账号分配提醒",notification.message_template.scene="AllOCATED_ACCOUNT_MSG",necessary="UNNECESSARY" where notification.message_template.type="sendAllocatedAccountMsg1";

update notification.message_template set notification.message_template.name="验证码提醒",notification.message_template.scene="VERIFICATION_CODE",necessary="NECESSARY" where notification.message_template.type="verificationCode";

update notification.message_template set notification.message_template.name="密码修改提醒",notification.message_template.scene="CHANGE_PASSWORD",necessary="NECESSARY" where notification.message_template.type="changePassword";

update notification.message_template set notification.message_template.name="注册提醒",notification.message_template.scene="REGISTER_MSG_SEND_TO_CUSTOMER",necessary="NECESSARY" where notification.message_template.type="registerMsgSendToCustomer";




