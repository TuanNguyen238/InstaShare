package com.example.instashare.Model;

public class Request {
    private String idrequest;
    private String idsend;
    private String idreceive;
    public Request() {
    }

    @Override
    public String toString() {
        return "Request{" +
                "idrequest='" + idrequest + '\'' +
                ", idsend='" + idsend + '\'' +
                ", idreceive='" + idreceive + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    public String getIdsend() {
        return idsend;
    }
    public void setIdsend(String idsend) {
        this.idsend = idsend;
    }

    public String getIdreceive() {
        return idreceive;
    }

    public void setIdreceive(String idreceive) {
        this.idreceive = idreceive;
    }

    public String getIdrequest() {
        return idrequest;
    }

    public void setIdrequest(String idrequest) {
        this.idrequest = idrequest;
    }

    public Request(String idrequest, String idsend, String idreceive, String state) {
        this.idrequest = idrequest;
        this.idsend = idsend;
        this.idreceive = idreceive;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private String state;

}
