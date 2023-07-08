export const randomFromList = <T>(list: T[]) => {
  return list[Math.floor(Math.random() * list.length)];
};

export const shuffle = <T>(array: T[]) => {
  let currentIndex = array.length,
    randomIndex;

  while (currentIndex != 0) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex--;

    [array[currentIndex], array[randomIndex]] = [
      array[randomIndex],
      array[currentIndex],
    ];
  }

  return array;
};
