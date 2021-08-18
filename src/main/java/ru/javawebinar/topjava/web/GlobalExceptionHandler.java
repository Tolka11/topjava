package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MessageSource msgSrc;

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView dataIntegrityViolationHandler(HttpServletRequest req, DataIntegrityViolationException e, Locale locale) throws Exception {
        log.error("Exception at request " + req.getRequestURL(), e);
        Throwable rootCause = ValidationUtil.getRootCause(e);

        String detail = "DataIntegrityViolationException";
        if (rootCause.toString().contains("meals_unique_user_datetime_idx")) {
            detail = msgSrc.getMessage("error.datetime", null, locale);
        }
        if (rootCause.toString().contains("users_unique_email_idx")) {
            detail = msgSrc.getMessage("error.email", null, locale);
        }

        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ModelAndView mav = new ModelAndView("exception",
                Map.of("exception", rootCause, "message", detail, "status", httpStatus));
        mav.setStatus(httpStatus);

        // Interceptor is not invoked, put userTo
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            mav.addObject("userTo", authorizedUser.getUserTo());
        }
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        log.error("Exception at request " + req.getRequestURL(), e);
        Throwable rootCause = ValidationUtil.getRootCause(e);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ModelAndView mav = new ModelAndView("exception",
                Map.of("exception", rootCause, "message", rootCause.toString(), "status", httpStatus));
        mav.setStatus(httpStatus);

        // Interceptor is not invoked, put userTo
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            mav.addObject("userTo", authorizedUser.getUserTo());
        }
        return mav;
    }
}
