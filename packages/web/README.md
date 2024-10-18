# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `yarn start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `yarn test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `yarn build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `yarn eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

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
