/* ===== AUTH UTILS ===== */
async function authFetch(url, options = {}) {
  const opts = {
    ...options,
    credentials: 'include',  // 쿠키 자동 첨부
    headers: {
      ...(options.headers || {}),
    },
  };

  let res = await fetch(url, opts);

  if (res.status === 401) {
    // access_token 만료 → refresh 시도
    const refreshRes = await fetch('/api/auth/refresh', {
      method: 'POST',
      credentials: 'include',
    });

    if (refreshRes.ok) {
      // 새 access_token이 쿠키로 발급됨 → 원래 요청 재시도
      res = await fetch(url, opts);
    } else {
      doLogout();
    }
  }

  return res;
}

async function doLogout() {
  try {
    await fetch('/api/auth/logout', {
      method: 'POST',
      credentials: 'include',
    });
  } catch { /* 네트워크 오류여도 로그아웃 진행 */ }
  window.location.href = 'login.html';
}

async function requireAuth() {
  const res = await fetch('/api/auth/me', { credentials: 'include' });
  if (!res.ok) { window.location.href = 'login.html'; return null; }
  return res.json(); // { email, nickname }
}

/* ===== HASH ROUTER ===== */
(function () {
  // Hash → HTML 파일 매핑
  const ROUTES = {
    '#/login':        'login.html',
    '#/register':     'register.html',
    '#/dashboard':    'dashboard.html',
    '#/vocab/new':    'vocab-new.html',
    '#/vocab/import': 'vocab-import.html',
    '#/community':    'community.html',
    '#/settings':          'settings.html',
    '#/forgot-password':   'forgot-password.html',
  };

  function navigate(hash) {
    // 정적 라우트 매칭
    const target = ROUTES[hash];
    if (target) { window.location.href = target; return; }

    // 동적 라우트: #/vocab/:id
    const vocabMatch = hash.match(/^#\/vocab\/(\d+)$/);
    if (vocabMatch) { window.location.href = `vocab.html?id=${vocabMatch[1]}`; return; }

    // 동적 라우트: #/test/:vocabId
    const testMatch = hash.match(/^#\/test\/(\d+)$/);
    if (testMatch) { window.location.href = `test.html?vocabId=${testMatch[1]}`; return; }

    // 동적 라우트: #/test/:id/result
    const resultMatch = hash.match(/^#\/test\/(\d+)\/result$/);
    if (resultMatch) { window.location.href = `test-result.html?vocabId=${resultMatch[1]}`; return; }
  }

  // 페이지 최초 진입 시 처리 (직접 URL에 hash가 붙은 경우)
  navigate(window.location.hash);

  // hash 변경 시 처리 (링크 클릭)
  window.addEventListener('hashchange', () => {
    navigate(window.location.hash);
  });
})();

/* ===== HAMBURGER ===== */
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');

hamburger.addEventListener('click', () => {
  mobileMenu.classList.toggle('open');
});

document.querySelectorAll('.nav__mobile a').forEach(link => {
  link.addEventListener('click', () => mobileMenu.classList.remove('open'));
});

/* ===== NAV SCROLL SHADOW ===== */
const nav = document.querySelector('.nav');
window.addEventListener('scroll', () => {
  nav.style.boxShadow = window.scrollY > 10
    ? '0 2px 20px rgba(0,0,0,.5)'
    : 'none';
}, { passive: true });

/* ===== SCROLL REVEAL ===== */
const revealEls = document.querySelectorAll(
  '.feature-card, .step, .community-card, .ai-card, .tag-item, .hero__stats .stat'
);
revealEls.forEach((el, i) => {
  el.classList.add('reveal');
  el.style.transitionDelay = `${(i % 6) * 60}ms`;
});

const observer = new IntersectionObserver(
  entries => entries.forEach(e => { if (e.isIntersecting) e.target.classList.add('visible'); }),
  { threshold: 0.12 }
);
revealEls.forEach(el => observer.observe(el));

/* ===== WORD CARD CYCLE ===== */
const words = [
  {
    tags: ['TOEIC', '명사'],
    word: 'implement',
    ipa: '/ˈɪmplɪmənt/',
    meaning: '시행하다 · 실행하다',
    example: 'The company will <em>implement</em> the new policy next quarter.',
    tip: '💡 impl(안에) + ment → 안으로 채워 넣다 → 실행하다',
  },
  {
    tags: ['수능', '형용사'],
    word: 'significant',
    ipa: '/sɪɡˈnɪfɪkənt/',
    meaning: '중요한 · 상당한',
    example: 'There was a <em>significant</em> improvement in test scores.',
    tip: '💡 sign(신호) + ific → 신호를 보낼 만큼 중요한',
  },
  {
    tags: ['TOEFL', '동사'],
    word: 'constitute',
    ipa: '/ˈkɒnstɪtjuːt/',
    meaning: '구성하다 · ~에 해당하다',
    example: 'Women <em>constitute</em> 40% of the workforce.',
    tip: '💡 con(함께) + stitute(세우다) → 함께 세워 구성하다',
  },
];

let wordIdx = 0;
const card = document.querySelector('.word-card');
const genBar = document.getElementById('generatingBar');

function cycleWord() {
  if (!card || !genBar) return;
  genBar.classList.add('active');

  setTimeout(() => {
    wordIdx = (wordIdx + 1) % words.length;
    const w = words[wordIdx];

    card.querySelector('.word-card__header').innerHTML =
      w.tags.map(t => `<span class="tag tag--${tagClass(t)}">${t}</span>`).join('');
    card.querySelector('.word-card__word').textContent = w.word;
    card.querySelector('.word-card__ipa').textContent = w.ipa;
    card.querySelector('.word-card__meaning').textContent = w.meaning;
    card.querySelector('.word-card__example').innerHTML = `"${w.example}"`;
    card.querySelector('.word-card__tip').textContent = w.tip;

    genBar.classList.remove('active');
  }, 1200);
}

function tagClass(tag) {
  if (tag === 'TOEIC') return 'toeic';
  if (tag === 'TOEFL') return 'toefl';
  if (tag === '수능') return 'csat';
  return 'noun';
}

setInterval(cycleWord, 4000);

/* ===== TAG CLICK TOGGLE ===== */
document.querySelectorAll('.tag-item').forEach(el => {
  el.addEventListener('click', () => {
    el.classList.toggle('tag-item--active');
  });
});
