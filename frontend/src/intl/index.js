import languages from 'misc/constants/languages';
import mixMessages from 'misc/intl/messages';
const DEFAULT_LANG = languages.en;

function getMessages(lang) {
  const defaultMessages = require('./messages.json');
  let messages;
  try {
    messages = lang === DEFAULT_LANG
      ? defaultMessages
      : require(`./messages.${lang.toLowerCase()}.json`);
  } catch (e) {
    messages = defaultMessages;
  }
  return mixMessages({
    defaultMessages,
    messages,
  });
}

export default getMessages;
