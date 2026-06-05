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
