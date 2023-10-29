export default (data) => {
  let newData = data;
  [...newData.matchAll(/!responsiveimg\((.*)\)/g)].forEach((match) => {
    const [...args] = match[1].split("|");
    newData = newData.replace(
      match[0],
      `<div style="${args[1].trim()}"><img src="${args[0].trim()}" style="width: 100%; height: 100%" /></div>`
    );
  });
  return newData;
};
