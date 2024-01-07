package org.warehouse.app.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Value("${logging.filter.max.log.lines: 5000}")
    private int maxLogLines;

    private static final String[] FILE_CONTENT_TYPES = {"audio", "video", "image", "multipart", "pdf", "zip", "msword", "octet-stream"};

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!(req instanceof ContentCachingRequestWrapper)) {
            req = new ContentCachingRequestWrapper((HttpServletRequest) req);
        }
        if (!(res instanceof ContentCachingResponseWrapper)) {
            res = new ContentCachingResponseWrapper((HttpServletResponse) res);
        }

        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);

            try {
                chain.doFilter(req, res);
                logRequest((HttpServletRequest) req);
            } finally {
                logResponse((HttpServletResponse) res);
                updateResponse((HttpServletResponse) res);
            }
        } finally {
            MDC.clear();
        }
    }

    private void logRequest(HttpServletRequest request) {
        log.info("Request[uri: {} , method: {} , address: {} , query: {} , body: {}]",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                request.getQueryString(),
                fileFilter(request.getContentType(), getRequestAsString(request))
        );
    }

    private void logResponse(HttpServletResponse response) {
        log.info("Response[status: {} , body: {}]",
                response.getStatus(),
                fileFilter(response.getContentType(), getResponseAsString(response))
        );
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (responseWrapper != null) {
            responseWrapper.copyBodyToResponse();
        }
    }

    private String fileFilter(String contentType, String body) {
        if (contentType != null && Arrays.stream(FILE_CONTENT_TYPES).anyMatch(contentType::contains)) {
            return "[file with content-type: " + contentType + "]";
        }
        return body;
    }

    private String getRequestAsString(HttpServletRequest request) {
        ContentCachingRequestWrapper wrappedRequest = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrappedRequest != null && wrappedRequest.getContentLength() > 0) {
            try {
                return new String(wrappedRequest.getContentAsByteArray(), 0, Math.min(wrappedRequest.getContentLength(), maxLogLines),
                        request.getCharacterEncoding());
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return "[unknown]";
    }

    private String getResponseAsString(HttpServletResponse response) {
        ContentCachingResponseWrapper wrappedResponse = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrappedResponse != null && wrappedResponse.getContentSize() > 0) {
            try {
                return new String(wrappedResponse.getContentAsByteArray(), 0, Math.min(wrappedResponse.getContentSize(), maxLogLines),
                        response.getCharacterEncoding());
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return "[unknown]";
    }
}
