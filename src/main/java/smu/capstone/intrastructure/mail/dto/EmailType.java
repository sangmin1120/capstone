package smu.capstone.intrastructure.mail.dto;

public enum EmailType {
    SIGNUP_CODE_MAIL("이메일 인증 코드입니다", "이메일 주소 확인", "아래 확인 코드를 회원가입 화면에서 입력해주세요."),
    PASSWORD_CODE_MAIL("비밀번호 찾기 코드입니다.", "인증 번호 확인", "아래 확인 코드를 인증 화면에 입력해주세요."),
    PASSWORD_RESET("비밀번호 재설정 코드입니다", "비밀번호 재설정", "아래 코드를 입력하여 새로운 비밀번호를 설정해주세요."),
    SCHEDULE_ALARM("[RehaLink] 일정 알림: ", "스케줄 알람", "스케줄 일정 알람입니다.");

    private final String subject;
    private final String title;
    private final String message;

    EmailType(String subject, String title, String message) {
        this.subject = subject;
        this.title = title;
        this.message = message;
    }

    public String getSubject() { return subject; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
}
