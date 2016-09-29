package com.teketys.templetickets.entities.delivery;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BranchesRequest {

    @SerializedName("records")
    private List<Branch> branches;

    private String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    private String statusText;

    public BranchesRequest(List<Branch> branches) {
        this.branches = branches;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BranchesRequest that = (BranchesRequest) o;

        return !(branches != null ? !branches.equals(that.branches) : that.branches != null);

    }

    @Override
    public int hashCode() {
        return branches != null ? branches.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BranchesRequest{" +
                "branches=" + branches +
                '}';
    }
}
