package com.waps.union_jd_api.controller;

import com.waps.utils.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/media_user")
public class JsMediaUserAPIController {

//    @Autowired
//    private JDMediaUserService jdMediaUserService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public void mediaUserList(
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

//        List<WapsJdUser> list = jdMediaUserService.listMediaUsers(page,size);
//        String json = JSONObject.toJSONString(list);
        ResponseUtils.write(response, "ok");
    }
}
