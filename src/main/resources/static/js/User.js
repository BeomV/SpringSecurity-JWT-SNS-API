async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        // 로그인 API 호출
        const response = await fetch('/api/v1/users/authenticate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
            credentials: "include"
        });

        if (response.ok) {
            const data = await response.json();
            const token = data.accessToken; // 서버에서 반환된 JWT 토큰

            if (!token) {
                alert('JWT Access Token Invalid');
                return;
            }

            // JWT를 LocalStorage에 저장
            localStorage.setItem('jwt', token);
            console.log('Received JWT:', token);

            // 로그인 성공 시 mypage로 이동
            window.location.href = '/user/mypage';
        } else {
            alert('Login failed: Invalid username or password.');
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('An error occurred. Please try again later.');
    }
}

// 로그인 버튼 클릭 이벤트
document.getElementById('loginButton').addEventListener('click', login);
