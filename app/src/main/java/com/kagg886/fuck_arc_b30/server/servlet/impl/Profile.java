package com.kagg886.fuck_arc_b30.server.servlet.impl;

import com.alibaba.fastjson2.JSON;
import com.kagg886.fuck_arc_b30.server.model.Best30Model;
import com.kagg886.fuck_arc_b30.server.model.Result;
import com.kagg886.fuck_arc_b30.server.model.UserProfile;
import com.kagg886.fuck_arc_b30.server.res.UserManager;
import com.kagg886.fuck_arc_b30.server.servlet.AbstractServlet;
import com.kagg886.fuck_arc_b30.util.ArcaeaMemReader;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;


/**
 * @author kagg886
 * @date 2023/8/13 11:52
 **/
public class Profile extends AbstractServlet {
    public static final Profile INSTANCE = new Profile();

    private Profile() {
        super("user/profile", Method.POST);
    }

    @Override
    public void onRequest0(AsyncHttpServerRequest request, AsyncHttpServerResponse response) throws Throwable {

        double ptt = ArcaeaMemReader.Profile.GetUserPtt();
        if (ptt == 0.0) {
            response.send(JSON.toJSONString(Result.ERR_PTT_SEARCH_FAILED));
            return;
        }

        double b30_ptt = Best30.getB30Models().stream().mapToDouble(Best30Model::getPtt).sum() / 30;

        double r10_ptt = ptt * 2 - b30_ptt;
        response.send(JSON.toJSONString(Result.OK(new UserProfile(UserManager.userName, ptt, b30_ptt, r10_ptt))));
        //response.send(ArcaeaMemReader.Test());
    }
}
