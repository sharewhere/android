package co.share.share.util;

public class Constants {


    /* keys for posting */
    public static final String SHAREABLE = "shareable";
    public static final String SHAREWHERE_USERNAME = "sharewhere_username";
    public static final String STATE_ID = "state_id";
    public static final String STATE_NAME = "state_name";
    public static final String SHAR_ID = "shar_id";
    public static final String SHAR_NAME = "shar_name";
    public static final String SHAR_PIC_NAME = "shar_pic_name";
    public static final String SHAR_DESC = "shar_desc";
    public static final String USERNAME = "username";

    /* share states */
    public static final String REQ = "requesting";
    public static final String REQ_OFR = "requested_received_offer";
    public static final String OFR = "offering";
    public static final String OFR_REQ = "offered_received_request";

    /* create sharable */
    public static final String CREATE_TYPE = "CREATE_TYPE";

    public enum CreateType {
        OFFER,
        REQUEST
    };


}
