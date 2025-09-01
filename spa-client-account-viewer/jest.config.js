module.exports = {
  preset: "jest-preset-angular",
  transform: {
    "^.+\\.(ts|js|html)$": "jest-preset-angular",
  },
  setupFiles: ["<rootDir>/jest.setup.js"],
};