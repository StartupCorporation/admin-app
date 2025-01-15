package com.deye.web.async.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AskedCallbackRequestMessage extends RabbitMqMessage {
    private AskedCallbackRequestPayload data;

    @Getter
    @Setter
    public static class AskedCallbackRequestPayload {
        private CustomerInformation customer;
        private String comment;
        private Boolean message_customer;

        @Getter
        @Setter
        public static class CustomerInformation {
            private String name;
            private String phone;
        }
    }
}
