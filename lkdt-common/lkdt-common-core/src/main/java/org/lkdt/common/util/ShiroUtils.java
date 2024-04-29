package org.lkdt.common.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.lkdt.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

public class ShiroUtils {
    @Autowired
    private static SessionDAO sessionDAO;

    public static Subject getSubjct() {
        return SecurityUtils.getSubject();
    }
    public static LoginUser getUser() {
        Object object = getSubjct().getPrincipal();
        return (LoginUser)object;
    }
    public static String getUserId() {
        return getUser().getId();
    }
    public static void logout() {
        getSubjct().logout();
    }
    public static boolean isAdmin() {
        if(getSubjct() == null || getUser() == null){
            return false;
        }
        return getUser().isAdmin();
    }

    public static boolean isZhiBan() {
        if(getSubjct() == null || getUser() == null || getUser().getRole_ids() == null){
            return false;
        }
//        return getUser().isAdmin() || getUser().getRole_ids().contains("1414785814046445570");
        return getUser().isAdmin() || getUser().getRole_ids().contains("1430713427969097730");
    }

    public static List<Principal> getPrinciples() {
        List<Principal> principals = null;
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        return principals;
    }
}
