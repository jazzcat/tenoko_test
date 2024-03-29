export const PATH = {
  BASE: '/',
  LOGIN: '/login',
  LOGIN_REGISTER: '/login/register',
  SLACK_LOGIN_SERVER: `https://slack.com/openid/connect/authorize?scope=openid,email,profile&response_type=code&redirect_uri=${process.env.SLACK_REDIRECT_URL}&client_id=${process.env.SLACK_CLIENT_ID}`,
  CREW_HOME: '/',
  INTERVIEW_APPLY: '/interview/apply',
  INTERVIEW_COMPLETE: '/interview/complete',
  COACH_INTERVIEW_CREATE: '/coach/interview/create',
  COACH_HOME: '/coach/home',
  MY_PAGE: '/mypage',
  OAUTH_REDIRECT: '/api/login',
  ACCESS_DENY: '/access/deny',
  NOT_FOUND: '*',
} as const;
