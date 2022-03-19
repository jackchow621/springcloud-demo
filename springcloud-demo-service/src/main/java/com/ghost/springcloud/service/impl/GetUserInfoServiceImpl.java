package com.ghost.springcloud.service.impl;


import com.ghost.springcloud.entity.User;
import com.ghost.springcloud.mapper.UserMapper;
import com.ghost.springcloud.service.GetUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;



/*
 *
 * @author paida 派哒 zeyu.pzy@alibaba-inc.com
 * @date 2020/10/27
 */

@Service
public class GetUserInfoServiceImpl implements GetUserInfoService{

    @Autowired
    protected UserMapper userMapper;

    @Override
    public void getUserInfoById(String id, Model model){


        //search by id, get UserInfo
        User user = userMapper.queryUserInfo(id);
        model.addAttribute("name", user.getId())
                .addAttribute("age", user.getAge())
                .addAttribute("height", user.getHeight())
                .addAttribute("weight", user.getWeight());
    }
}
