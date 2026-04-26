export const getToken = () => {
  return localStorage.getItem('token') || ''
}

export const getUserInfo = () => {
  const str = localStorage.getItem('userInfo')
  if (!str) return null
  try {
    return JSON.parse(str)
  } catch (e) {
    return null
  }
}

export const clearLoginInfo = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
}

export const isLoggedIn = () => {
  return !!getToken()
}