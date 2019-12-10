package org.itrunner.wechat.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.itrunner.wechat.util.JsonUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Slf4j
public class WeChatOAuth2User implements OAuth2User {
    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String[] privilege;

    @JsonIgnore
    private Set<GrantedAuthority> authorities = new HashSet<>();
    @JsonIgnore
    private Map<String, Object> attributes;
    @JsonIgnore
    private String nameAttributeKey;

    public static WeChatOAuth2User build(String json, String userNameAttributeName) {
        try {
            WeChatOAuth2User user = JsonUtils.parseJson(json, WeChatOAuth2User.class);
            user.nameAttributeKey = userNameAttributeName;
            user.setAttributes();
            user.setAuthorities();

            return user;
        } catch (JsonProcessingException e) {
            log.error("An error occurred while attempting to parse the weixin User Info Response: " + e.getMessage());
            return null;
        }
    }

    private void setAttributes() {
        attributes = new HashMap<>();

        this.attributes.put("openid", openid);
        this.attributes.put("nickname", nickname);
        this.attributes.put("sex", sex);
        this.attributes.put("language", language);
        this.attributes.put("city", city);
        this.attributes.put("province", province);
        this.attributes.put("country", country);
        this.attributes.put("headimgurl", headimgurl);
    }

    private void setAuthorities() {
        authorities = new LinkedHashSet<>();
        for (String authority : privilege) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return this.getAttribute(this.nameAttributeKey).toString();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String[] getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String[] privilege) {
        this.privilege = privilege;
    }
}
