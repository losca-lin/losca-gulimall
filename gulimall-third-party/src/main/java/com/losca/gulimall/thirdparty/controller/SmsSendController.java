package com.losca.gulimall.thirdparty.controller;

import com.losca.common.utils.R;
import com.losca.gulimall.thirdparty.component.SmsComponent;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Resource
    SmsComponent smsComponent;

    /**
     * 提供给别的服务进行调用
     * @param phone
     * @param code
     * @return
     */
    @PostMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        //发送验证码
        smsComponent.sendSms(phone, code);
        return R.ok();
    }
}
