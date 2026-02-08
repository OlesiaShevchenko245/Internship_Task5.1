const mixMessages = ({
  defaultMessages,
  messages,
}) => {
  return Object
    .entries(defaultMessages)
    .reduce((result, [defaultMessageKey, defaultMessageText]) => ({
      ...result,
      [defaultMessageKey]: messages[defaultMessageKey] || defaultMessageText,
    }), {});
};

export default mixMessages;