package com.yujian.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yujian.reggie.common.R;
import com.yujian.reggie.entity.User;
import com.yujian.reggie.service.UserService;
import com.yujian.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}",code);

            session.setAttribute(phone,code);

            return R.success("手机验证码短信发送成功");

        }

        //生成随机四位验证码
        //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

        return R.success("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的短信验证码
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的对比（页面提交的验证码和session中保存的验证码对比）
        if(codeInSession != null && codeInSession.equals(code)){
            //如果能够对比成功，说明登陆成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }
}
