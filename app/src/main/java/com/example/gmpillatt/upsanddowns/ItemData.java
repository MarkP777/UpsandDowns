package com.example.gmpillatt.upsanddowns;

/**
 * Created by gmpillatt on 14/01/2017.
 */

public class ItemData {

    private String left;
    private String right;
    private String centre;
    private Integer textColor;
    private Integer dBId;

    public ItemData(String left, String centre, String right, Integer textColor, Integer dBId) {
        this.left = left;
        this.right = right;
        this.centre = centre;
        this.textColor = textColor;
        this.dBId = dBId;
    }

    /* Set methods not used

    public void setLeft (String left){
        this.left=left;

    }

    public void setRight (String right){
        this.right=right;
    }

    public void setCentre (String centre){
        this.centre=centre;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public void setDBId(Integer dBId) {
        this.dBId = dBId;
    }
    */

    public String getLeft(){
        return this.left;
    }

    public String getRight(){
        return this.right;
    }

    public String getCentre(){
        return this.centre;
    }

    public Integer getTextColor() {
        return this.textColor;
    }

    public Integer getdBId() {
        return this.dBId;
    }






}
