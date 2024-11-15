    async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
    // /authenticate 엔드포인트 호출
    const response = await fetch('/api/v1/users/authenticate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
});

    if (response.ok) {
    const data = await response.json();
    const token = data.accessToken;// 서버에서 반환된 accessToken 사용
    if (!token) {
    alert('JWT Access Token Invalid');
    return;
}
    localStorage.setItem('jwt', token); // JWT 저장
    console.log('Received JWT:', token);

} else {
    alert('Login failed: Invalid username or password.');
}
} catch (error) {
    console.error('Error during login:', error);
    alert('An error occurred. Please try again later.');
}
}


