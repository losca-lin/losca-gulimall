package com.losca.gulimall.auth.control;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.losca.common.exception.BizCodeEnum;
import com.losca.common.utils.R;
import com.losca.gulimall.auth.feign.SmsService;
import com.losca.gulimall.auth.vo.UserRegisterVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.losca.common.constant.AuthServerConstant.SMS_CODE_CACHE_PREFIX;

@Controller
public class LoginController {
    @Resource
    SmsService smsService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    //@GetMapping("/login.html")
    //public String login(ModelMap map) {
    //    return "login";
    //}
    //
    //@GetMapping("/reg.html")
    //public String reg(){
    //    return "reg";
    //}
    @PostMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone")String phone){
        //接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(SMS_CODE_CACHE_PREFIX + phone);
        if (StrUtil.isNotBlank(redisCode)){
            String s = redisCode.split("_")[1];
            long currentTime = Long.parseLong(s);
            if (System.currentTimeMillis() - currentTime < 60000){
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
            }
        }
        String code = RandomUtil.randomNumbers(5);
        stringRedisTemplate.opsForValue().set(SMS_CODE_CACHE_PREFIX+phone,code+ "_" + System.currentTimeMillis(),10, TimeUnit.MINUTES);
        return smsService.sendCode(phone, code);
    }

    /**
     *
     * TODO: 重定向携带数据：利用session原理，将数据放在session中。
     * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉
     * TODO：分布下session问题
     * RedirectAttributes：重定向也可以保留数据，不会丢失
     * 用户注册
     * @return
     */
    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo vos, BindingResult result,
                           RedirectAttributes attributes) {

        //如果有错误回到注册页面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors",errors);
            //校验出错回到注册页面
            return "redirect:http://auth.mall.com/reg.html";
            //return "forward:/reg.html";
            //不采用转发，转发一刷新会显示是否重新提交表单
            //return "reg";
        }
        return "redirect:/login.html";

    }



}
