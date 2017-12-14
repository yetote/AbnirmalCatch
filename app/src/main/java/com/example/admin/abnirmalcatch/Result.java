package com.example.admin.abnirmalcatch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * com.example.admin.abnirmalcatch
 *
 * @author Swg
 * @date 2017/12/14 14:59
 */
public class Result {

    @SerializedName("result")
    @Expose
    private String result;

    /**
     * @return The result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result The result
     */
    public void setResult(String result) {
        this.result = result;
    }

}