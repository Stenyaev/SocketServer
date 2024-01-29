package ru.stenyaev.Main.enums;

public enum Resp {
    RESPONSE_GET_EV_STATE("{\"getEvState\":{\"address\":{\"addressType\":\"STANDART\", \"value\":\"GBT\"},\"soc\":64,\"voltage\":473.2,\"current\":72.2}}"),
    RESPONSE_GET_PARAMETER("{\"getParameters\":{\"address\":{\"addressType\":\"UNIT\", \"value\":\"1\"},\"voltage\":473.2,\"current\":72.2}}"),
    RESPONSE_GET_STATE("{\"getState\":{\"address\":{\"addressType\":\"GROUP\", \"value\":\"2\"},\"state\":false}}");

    private final String resp;

    Resp(String resp) {
        this.resp = resp;
    }

    public String getResp() {
        return resp;
    }
}
