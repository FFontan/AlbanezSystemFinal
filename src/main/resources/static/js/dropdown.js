document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('.dropdown').forEach(dropdown => {
    dropdown.addEventListener('mouseenter', () => {
      const dropdownContent = dropdown.querySelector('.dropdown-content');
      const rect = dropdown.getBoundingClientRect();

      dropdownContent.style.display = 'flex';
      dropdownContent.style.top = rect.top + 'px';
      dropdownContent.style.left = rect.right + 'px';
    });
    dropdown.addEventListener('mouseleave', () => {
      const dropdownContent = dropdown.querySelector('.dropdown-content');
      dropdownContent.style.display = 'none';
    });
  });
});
