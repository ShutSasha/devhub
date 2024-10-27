# Project setup

add .env to root of the project. Variables require in Sasha

### ESLint Setup Guide

- pre-requirements

Most editors, such as VSCode, can automatically detect ESLint configuration. **Ensure you have the ESLint plugin installed in your IDE** so that linting issues are highlighted while you code.

If you don't have ESLint configured yet, follow the steps below to set it up:

1. **Generate ESLint Configuration:**
   Run the following command in your terminal to create the ESLint config file:

   ```bash
   yarn create @eslint/config
   ```

   During the setup, you will be prompted to select various options—choose the ones that suit your project's needs (e.g., framework type, TypeScript support, formatting rules).

2. **Install Required Packages:**
   After the initial setup, ESLint will prompt you to install several mandatory packages. Here’s a list of packages that were installed in my case:

   - `"@eslint/js": "^9.12.0"`
   - `"eslint": "^9.12.0"`
   - `"eslint-plugin-react": "^7.37.1"`
   - `"globals": "^15.10.0"`

3. **Full Dependencies List:**
   If you are using **TypeScript**, you will also need some additional packages. Here’s the complete list:

   ```json
   {
     "@eslint/js": "^9.12.0",
     "@types/eslint__js": "^8.42.3",
     "@types/react-router-dom": "^5.3.3",
     "@typescript-eslint/eslint-plugin": "^8.8.0",
     "@typescript-eslint/parser": "^8.8.0",
     "eslint": "^9.12.0",
     "eslint-config-prettier": "^9.1.0",
     "eslint-plugin-import": "^2.31.0",
     "eslint-plugin-prettier": "^5.2.1",
     "eslint-plugin-react": "^7.37.1",
     "globals": "^15.10.0"
   }
   ```

4. **TypeScript-Specific Setup:**
   If you’re working with TypeScript, ensure you also install the `@types/eslint__js` package. This is crucial to make TypeScript and ESLint work seamlessly together.

Now you should have a fully working ESLint configuration for your project!

### Explanation of eslint packages

- eslint: the core ESLint package.

- eslint-plugin-react: plugin for React-specific linting rules.

- eslint-plugin-react-hooks: plugin to ensure the rules of hooks are followed.

- eslint-plugin-jsx-a11y: plugin to ensure accessibility rules in JSX.

- eslint-plugin-import: plugin to manage ES6+ import/export syntax rules.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).
