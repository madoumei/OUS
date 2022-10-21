package com.utils.ExceptionUtils;

import com.config.qicool.common.utils.StringUtils;
import com.utils.SysLog;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;

@WebFilter(filterName = "httpServletRequestReplacedFilter", urlPatterns = "/*")
public class HttpServletRequestReplacedFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        BodyReaderHttpServletRequestWrapper requestWrapper = null;
        String method = "GET";
        String param = "";
        XssAndSqlHttpServletRequestWrapper xssRequest = null;
        if (request instanceof HttpServletRequest) {
            try {
                requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);
                method = ((HttpServletRequest) request).getMethod();
                xssRequest = new XssAndSqlHttpServletRequestWrapper((HttpServletRequest) request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //xss and sql 攻击检测,可以添加例外
        if ("POST".equalsIgnoreCase(method)
                && !"/qcvisit/createPdfSign".equals(requestWrapper.getRequestURI())
                && !"/qcvisit/addUserTemplate".equals(requestWrapper.getRequestURI())
                && !"/qcvisit/updateUserTemplate".equals(requestWrapper.getRequestURI())
                && !"/qcvisit/updateSecureProtocol".equals(requestWrapper.getRequestURI())
                && !"/qcvisit/updateSupplementAppointment".equals(requestWrapper.getRequestURI())) {
            param = RequestUtils.getRequestJsonString(requestWrapper);
            if(StringUtils.isNotBlank(param)){
                if(XssAndSqlHttpServletRequestWrapper.checkXSSAndSql(param)){
                    SysLog.error("checkXSSAndSql failed "+requestWrapper.getRequestURI()+" "+param);
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.write("{\"status\":3000,\"reason\":\"您所访问的页面请求中有违反安全规则元素存在，拒绝访问!\"}");
                    return;
                }
            }
        }
        if (xssRequest.checkParameter()) {
            SysLog.error("checkXSSAndSql2 failed "+requestWrapper.getRequestURI()+" "+param);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("{\"status\":3000,\"reason\":\"您所访问的页面请求中有违反安全规则元素存在，拒绝访问!\"}");
            return;
        }

        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }

    }

    // 获取request请求body中参数
    public static String getBodyString(BufferedReader br) {
        String inputLine;
        String str = "";
        try {
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return str;

    }

    @Override
    public void destroy() {
    }
}
