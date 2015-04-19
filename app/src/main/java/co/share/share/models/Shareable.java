package co.share.share.models;

import java.io.Serializable;

import co.share.share.util.Constants;

public class Shareable implements Serializable{

    public String username;
    public int shar_id;
    public String shar_name;
    public String description;
    public String creation_date;
    public String shar_pic_name;
    public String zip_code;
    public String state_name;
    public String start_date;
    public String end_date;
    public int state_id;


    public String[] pastTense = {"borrowed", "offered"};
    public String[] presentTense = {"borrow", "offer"};

    /**
     * returns whether or not the object is a request or offer
     * @return 0 if request 1 if offer
     */
    public int getSharableType() {
        switch (this.state_name) {
            case Constants.REQ:
            case Constants.REQ_OFR:
                return 0;
            case Constants.OFR:
            case Constants.OFR_REQ:
                return 1;
            default:
                return -1;
        }
    }


    /*
    "shar_id": 10,
            "shar_name": "vxgxhhb",
            "description": "b ccfjx",
            "username": "tj",
            "creation_date": "2015-04-12 13:50:48",
            "shar_pic_name": "8a4d0e414494726b6b8c3db5a995553e",
            "state_id": 3,
            "state_name": "offering",
            "zip_code": "32816"

    */
}
