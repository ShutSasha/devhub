export const base64ToUrl = (base64String: string) => {
  // Убираем префикс 'data:image/svg+xml;base64,' (если он присутствует)
  const base64Data = base64String.split(',')[1]

  try {
    // Декодируем Base64 в байты
    const byteCharacters = atob(base64Data)

    // Создаем массив байтов
    const byteArrays = []

    for (let offset = 0; offset < byteCharacters.length; offset++) {
      byteArrays.push(byteCharacters.charCodeAt(offset))
    }

    // Создаем объект Blob из байтов
    const blob = new Blob([new Uint8Array(byteArrays)], { type: 'image/svg+xml' })

    // Возвращаем URL для изображения
    return URL.createObjectURL(blob)
  } catch (e) {
    console.error('Error decoding Base64 string:', e)
  }
}
