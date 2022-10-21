package com.utils.ExceptionUtils;

import java.io.*;
import java.util.Enumeration;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws Exception {
        super(request);
        String requestURI = request.getRequestURI();
        if (!"/qcvisit/Upload".equals(requestURI)
                && !"/qcvisit/UploadLaborFile".equals(requestURI)
                && !"/qcvisit/UploadAB".equals(requestURI)
                && !"/qcvisit/UploadSubaccountEmp".equals(requestURI)
                && !"/qcvisit/UploadApponintment".equals(requestURI)
                && !"/qcvisit/UploadResidentVisitor".equals(requestURI)
                && !"/qcvisit/UploadSubAccount".equals(requestURI)
                && !"/qcvisit/UploadExamTemplate".equals(requestURI)
                && !"/qcvisit/uploadSubAccountLogo".equals(requestURI)
                && !"/qcvisit/UploadResidentFile".equals(requestURI)
                && !"/qcvisit/deployeeProcess".equals(requestURI)
                && !"/qcvisit/UploadLog".equals(requestURI)) {
            //传输文件时运行下面函数会导致接口报错
            body = readBytes(request.getInputStream());
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if(body==null){
            return null;

        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    public static byte[] readBytes(InputStream is) throws Exception {
        byte[] buffer=new byte[1024];
        int len=0;
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        while((len=is.read(buffer))!=-1){
            bos.write(buffer,0,len);
        }
        bos.flush();
        is.mark(0);
        return bos.toByteArray();
    }

    public byte[] getBody(){
        return body;
    }
}
