const navBrowse = document.getElementById('nav-browse');
const navFavs = document.getElementById('nav-favs');
const navAdmin = document.getElementById('nav-admin');

const viewBrowse = document.getElementById('view-browse');
const viewFavs = document.getElementById('view-favs');
const viewAdmin = document.getElementById('view-admin');

const grid = document.getElementById('grid');
const favContainer = document.getElementById('favGrid');
const message = document.getElementById('message');
const favMsg = document.getElementById('favMsg');
const loading = document.getElementById('loading');

const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const pageNumber = document.getElementById('pageNum');

const backToBrowseBtn = document.getElementById('backToBrowse');
const clearFavoritesBtn = document.getElementById('clearFavorites');

const pageNumberFav = document.getElementById('pageNumFav');
const prevBtnFav = document.getElementById('prevBtnFav');
const nextBtnFav = document.getElementById('nextBtnFav');

let currentPage = 1;
let favCurrentPage = 1;
const limit = 6;

function showBrowse() {
    navBrowse.classList.add('active');
    navFavs.classList.remove('active');
    if (navAdmin) navAdmin.classList.remove('active');
    viewBrowse.style.display = 'block';
    viewFavs.style.display = 'none';
    if (viewAdmin) viewAdmin.style.display = 'none';
}
function showFavs() {
    navFavs.classList.add('active');
    navBrowse.classList.remove('active');
    if (navAdmin) navAdmin.classList.remove('active');
    viewFavs.style.display = 'block';
    viewBrowse.style.display = 'none';
    if (viewAdmin) viewAdmin.style.display = 'none';
}
function showAdmin() {
    if (!viewAdmin) return;
    if (navAdmin) {
        navAdmin.classList.add('active');
        navBrowse.classList.remove('active');
        navFavs.classList.remove('active');
    }
    viewAdmin.style.display = 'block';
    viewBrowse.style.display = 'none';
    viewFavs.style.display = 'none';
}

function toggleLoading(show) {
    loading.style.display = show ? 'block' : 'none';
}

function updateBrowsePagination(catsCount) {
    pageNumber.textContent = currentPage;
    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = catsCount < limit;
}

function updateFavoritesPagination(total) {
    pageNumberFav.textContent = favCurrentPage;
    prevBtnFav.disabled = favCurrentPage === 1;
    nextBtnFav.disabled = favCurrentPage * limit >= total;
}

async function fetchCats() {
    message.textContent = '';
    toggleLoading(true);
    const skip = (currentPage - 1) * limit;
    const url = `https://cataas.com/api/cats?skip=${skip}&limit=${limit}`;
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`Failed to fetch cats. Status: ${response.status}`);
        const cats = await response.json();
        toggleLoading(false);
        if (cats.length === 0 && currentPage > 1) {
            currentPage--;
            return;
        }
        let dataFavorites = [];
        try {
            const favRes = await authFetch(`/favorite`);
            if (favRes.ok) {
                const favJson = await favRes.json();
                dataFavorites = Array.isArray(favJson)
                    ? favJson
                    : (Array.isArray(favJson?.content) ? favJson.content : []);
            } else if (favRes.status !== 401) {
                console.warn('Favorite fetch failed with status', favRes.status);
            }
        } catch (e) {
            console.warn('Favorite fetch error:', e);
        }
        renderCatsGrid(cats, dataFavorites);
        updateBrowsePagination(cats.length);
    } catch (error) {
        toggleLoading(false);
        message.textContent = `Error: ${error.message}. Please try again.`;
        console.error(error);
    }
}

function renderCatsGrid(cats, dataFavorites) {
    grid.innerHTML = '';
    const favoritesArr = Array.isArray(dataFavorites)
        ? dataFavorites
        : (Array.isArray(dataFavorites?.content) ? dataFavorites.content : []);
    const favoritesSet = new Set(favoritesArr.map(f => f.catId));
    cats.forEach(cat => {
        const card = document.createElement('div');
        card.classList.add('card');
        card.style.position = 'relative';
        const img = document.createElement('img');
        img.src = `https://cataas.com/cat/${cat.id}`;
        img.alt = 'Cat Image';
        card.appendChild(img);
        const btn = document.createElement('button');
        btn.classList.add('fav-btn');
        const isFav = favoritesSet.has(cat.id);
        btn.textContent = isFav ? '★' : '☆';
        btn.title = isFav ? 'Remove from favorite' : 'Add to favorite';
        if (isFav) btn.classList.add('active');
        btn.addEventListener('click', () => toggleFavorite(cat.id, btn));
        card.appendChild(btn);
        grid.appendChild(card);
    });
    pageNumber.textContent = currentPage;
}

async function fetchFavorites() {
    favContainer.innerHTML = '';
    favMsg.textContent = '';
    try {
        const res = await authFetch(`/favorite`);
        if (res.status === 401) {
            favMsg.textContent = 'Please log in to see your favorites.';
            updateFavoritesPagination(0);
            return;
        }
        if (!res.ok) throw new Error(`Status ${res.status}`);
        const favJson = await res.json();
        const data = Array.isArray(favJson)
            ? favJson
            : (Array.isArray(favJson?.content) ? favJson.content : []);
        if (data.length === 0) {
            favMsg.textContent = 'No favorites yet. Go add some!';
            prevBtnFav.disabled = true;
            nextBtnFav.disabled = true;
            updateFavoritesPagination(0);
            return;
        }
        const start = (favCurrentPage - 1) * limit;
        const end = start + limit;
        const favsToShow = data.slice(start, end);
        favsToShow.forEach(fav => {
            const card = createFavoriteCard(fav.id, fav.catId);
            favContainer.appendChild(card);
        });
        updateFavoritesPagination(data.length);
    } catch (err) {
        favMsg.textContent = 'Failed to load favorites.';
        console.error(err);
    }
}

function createFavoriteCard(favId, catId) {
    const card = document.createElement('div');
    card.classList.add('card');
    card.style.position = 'relative';
    const img = document.createElement('img');
    img.src = `https://cataas.com/cat/${catId}`;
    img.alt = 'Cat Image';
    card.appendChild(img);
    const btn = document.createElement('button');
    btn.classList.add('fav-btn');
    btn.textContent = '★';
    btn.title = 'Remove from favorite';
    btn.classList.add('active');
    btn.addEventListener('click', async () => {
        try {
            const delRes = await authFetch(`/favorite/${favId}`, { method: 'DELETE' });
            if (!delRes.ok) throw new Error('Delete failed');
            await fetchFavorites();
            await fetchCats();
        } catch (e) {
            console.error(e);
            alert('Failed to remove favorite.');
        }
    });
    card.appendChild(btn);
    return card;
}

async function toggleFavorite(catId, btn) {
    try {
        let favorites = [];
        try {
            const favRes = await authFetch(`/favorite`);
            if (favRes.ok) {
                const favJson = await favRes.json();
                favorites = Array.isArray(favJson)
                    ? favJson
                    : (Array.isArray(favJson?.content) ? favJson.content : []);
            }
        } catch {}
        const existing = favorites.find(f => f.catId === catId);
        if (existing) {
            const delRes = await authFetch(`/favorite/${existing.id}`, { method: 'DELETE' });
            if (!delRes.ok) throw new Error('Failed to remove favorite');
            btn.textContent = '☆';
            btn.title = 'Add to favorite';
            btn.classList.remove('active');
        } else {
            const body = JSON.stringify({
                catId,
                catImage: `https://cataas.com/cat/${catId}`
            });
            const createRes = await authFetch(`/favorite`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body
            });
            if (!createRes.ok) throw new Error('Failed to add favorite');
            btn.textContent = '★';
            btn.title = 'Remove from favorite';
            btn.classList.add('active');
        }
        if (viewFavs.style.display !== 'none') {
            await fetchFavorites();
        }
    } catch (e) {
        console.error(e);
        alert('Failed to toggle favorite.');
    }
}

navBrowse.addEventListener('click', (e) => {
    e.preventDefault();
    showBrowse();
    fetchCats();
});
navFavs.addEventListener('click', (e) => {
    e.preventDefault();
    showFavs();
    favCurrentPage = 1;
    fetchFavorites();
});
if (navAdmin) {
    navAdmin.addEventListener('click', (e) => {
        e.preventDefault();
        showAdmin();
    });
}

prevBtn.addEventListener('click', () => {
    if (currentPage > 1) { currentPage--; fetchCats(); }
});
nextBtn.addEventListener('click', () => {
    currentPage++; fetchCats();
});

prevBtnFav.addEventListener('click', () => {
    if (favCurrentPage > 1) { favCurrentPage--; fetchFavorites(); }
});
nextBtnFav.addEventListener('click', () => {
    favCurrentPage++; fetchFavorites();
});

backToBrowseBtn?.addEventListener('click', () => {
    showBrowse();
    fetchCats();
});

clearFavoritesBtn?.addEventListener('click', async () => {
    try {
        const res = await authFetch(`/favorite`);
        if (!res.ok) return;
        const favJson = await res.json();
        const favorites = Array.isArray(favJson)
            ? favJson
            : (Array.isArray(favJson?.content) ? favJson.content : []);
        for (const f of favorites) {
            await authFetch(`/favorite/${f.id}`, { method: 'DELETE' });
        }
        await fetchFavorites();
        await fetchCats();
    } catch (e) {
        console.error(e);
        alert('Failed to clear favorites.');
    }
});

showBrowse();
fetchCats();
