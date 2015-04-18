package co.share.share.util;

public class Constants {


    public static final String SHAREABLE = "co.share.shareable";
    public static final String STATE_ID = "co.share.state_id";
    public static final String STATE_NAME = "co.share.state_name";
    public static final String SHAR_ID = "co.share.shar_id";
    public static final String SHAR_NAME = "co.share.shar_name";
    public static final String SHAR_PIC_NAME = "co.share.shar_pic_name";
    public static final String SHAR_DESC = "co.share.shar_desc";
    public static final String USERNAME = "co.share.username";

    public static final String REQ = "requesting";
    public static final String REQ_OFR = "request_received_offer";
    public static final String OFR = "offering";
    public static final String OFR_REQ = "offer_received_request";

    enum ShareStates {
        requesting,
        requested_received_offer,
        offering,
        offered_received_request
    };

}
