import constantService from './constantService';

export default class backendService {
  static getJwt(username, password) {
    return fetch(`${constantService.baseUrl}/profile/login`, {
      method: 'GET',
      headers: {
        Authorization: `Basic ${btoa(`${username}:${password}`)}`,
      },
    });
  }

  static signUp(username, email, password) {
    return fetch(`${constantService.baseUrl}/profile/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username,
        email,
        password,
      }),
    });
  }

  static getOwn(jwt) {
    return fetch(`${constantService.baseUrl}/app/own`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  }
}
