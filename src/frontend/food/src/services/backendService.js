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

  static getSaved(jwt, page = null, pageSize = null) {
    return fetch(`${constantService.baseUrl}/app/saved?${page ? `page=${page}` : ''}&${pageSize ? `page_size=${pageSize}` : ''}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
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

  static getRecipe(id) {
    return fetch(`${constantService.baseUrl}/app/recipe/${id}`, {
      method: 'GET',
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

  static deleteRecipe(jwt, id) {
    return fetch(`${constantService.baseUrl}/app/recipe/${id}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  }

  static saveToUser(jwt, id) {
    return fetch(`${constantService.baseUrl}/app/save/${id}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  }

  static unsaveFromUser(jwt, id) {
    return fetch(`${constantService.baseUrl}/app/unsave/${id}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    });
  }

  static getRecipesByTags(names, page = null, pageSize = null) {
    return fetch(`${constantService.baseUrl}/app/recipe/tags`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ names, page, page_size: pageSize }),
    });
  }

  static getRecipesByIngredients(names, page = null, pageSize = null) {
    return fetch(`${constantService.baseUrl}/app/recipe/ingredients`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ names, page, page_size: pageSize }),
    });
  }

  static getAllRecipes(page = null, pageSize = null) {
    return fetch(`${constantService.baseUrl}/app/recipe?${page ? `page=${page}` : ''}&${pageSize ? `page_size=${pageSize}` : ''}`, {
      method: 'GET',
    });
  }

  static getRecipesByUser(user, page = null, pageSize = null) {
    return fetch(`${constantService.baseUrl}/app/recipe/user?username=${user}&${page ? `page=${page}` : ''}&${pageSize ? `page_size=${pageSize}` : ''}`, {
      method: 'GET',
    });
  }

  static getUsers(string, page = null, pageSize = null) {
    return fetch(`${constantService.baseUrl}/app/user/string?string=${string}&${page ? `page=${page}` : ''}&${pageSize ? `page_size=${pageSize}` : ''}`, {
      method: 'GET',
    });
  }

  static searchRecipes(string, page = null, pageSize = null) {
    return fetch(`${constantService.baseUrl}/app/recipe/search?string=${string}&${page ? `page=${page}` : ''}&${pageSize ? `page_size=${pageSize}` : ''}`, {
      method: 'GET',
    });
  }
}
