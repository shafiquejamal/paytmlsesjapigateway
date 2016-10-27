const PROTOCOL = process.env.CRAUTH_PROTOCOL;
const API_SERVER = process.env.CRAUTH_API_SERVER;
const WS_PROTOCOL = process.env.CRAUTH_WS_PROTOCOL;

export const ROOT_URL = `${PROTOCOL}://${API_SERVER}`;
export const WS_ROOT_URL = `${WS_PROTOCOL}://${API_SERVER}`;