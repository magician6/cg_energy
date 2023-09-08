package cn.wilmar.cg_energy.exception;

/**
 * @Author: fengzixin
 * @Date: 2022/8/19
 */
public class ServiceException extends RuntimeException {

    /**
     * 异常描述
     */
    private String message;

    public ServiceException(Throwable cause, String message, Object... args) {
        super(cause);
        this.message = String.format(message, args);
    }

    public ServiceException(String message, Object... code) {
        this.message = String.format(message, code);
    }

    public ServiceException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
