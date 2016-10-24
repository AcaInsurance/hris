package aca.com.hris.PojoModel;

import org.androidannotations.annotations.Click;

/**
 * Created by Marsel on 24/2/2016.
 */
public class Clicked {
    public boolean approvedClick;
    public boolean rejectedClick;
    public boolean approvedReviewedClick;
    public boolean rejectedReviewedClick;
    public String remark;

    public Clicked () {
        approvedClick = false;
        rejectedClick = false;
        approvedReviewedClick = false;
        rejectedReviewedClick = false;
        remark = "";

    }
}
