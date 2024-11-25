export const parseTagsToUI = (tags: string[]): string[] => {
  return tags.map(item => '#' + item)
}
