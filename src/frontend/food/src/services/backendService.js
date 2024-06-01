import FormData from 'form-data';
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

  static getDaily() {
    return fetch(`${constantService.baseUrl}/app/daily`, {
      method: 'GET',
    });
  }

  static getTags() {
    return fetch(`${constantService.baseUrl}/app/tag`, {
      method: 'GET',
    });
  }

  static getTagsByNames(jwt, names) {
    return fetch(`${constantService.baseUrl}/app/tag/names`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${jwt}`,
      },
      body: JSON.stringify(names),
    });
  }

  static getIngredients() {
    return fetch(`${constantService.baseUrl}/app/ingredient`, {
      method: 'GET',
    });
  }

  static putTags(jwt, tags) {
    return fetch(`${constantService.baseUrl}/app/tags`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${jwt}`,
      },
      body: JSON.stringify({
        names: tags,
      }),
    });
  }

  static putIngredients(jwt, ingredients) {
    return fetch(`${constantService.baseUrl}/app/ingredients`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${jwt}`,
      },
      body: JSON.stringify({
        ingredients,
      }),
    });
  }

  static putImage(jwt, image) {
    const formdata = new FormData();
    formdata.append('image', image);
    return fetch(`${constantService.baseUrl}/app/image`, {
      method: 'PUT',
      headers: {
        // do not set content-type, it will be set automatically with the boundary
        Authorization: `Bearer ${jwt}`,
      },
      body: formdata,
    });
  }

  static putRecipe(jwt, recipe) {
    return fetch(`${constantService.baseUrl}/app/recipe`, {
      method: 'PUT',
      headers: {
        'content-type': 'application/json',
        Authorization: `Bearer ${jwt}`,
      },
      body: JSON.stringify(recipe),
    });
  }
}
