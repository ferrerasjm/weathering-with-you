function startClock() {
  const dateTimeEl = document.getElementById('date-time-text');

  function updateClock() {
    const now = new Date();

    const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    const day = days[now.getDay()];

    const date = now.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });

    let hours = now.getHours();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12 || 12;

    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    const time = `${hours}:${minutes}:${seconds} ${ampm}`;
    dateTimeEl.innerHTML = `${day} / ${date} / ${time}`;
  }

  updateClock();
  setInterval(updateClock, 1000);
}

startClock();
