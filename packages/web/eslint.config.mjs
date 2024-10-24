import eslintPluginReact from 'eslint-plugin-react'
import eslintPluginPrettier from 'eslint-plugin-prettier'
import eslintPluginImport from 'eslint-plugin-import'
import eslintTypescriptPlugin from '@typescript-eslint/eslint-plugin'
import typescriptEslintParser from '@typescript-eslint/parser'

export default [
	{
		files: ['src/**/*.{ts,tsx,js,jsx}'],
		languageOptions: {
			parser: typescriptEslintParser,
			ecmaVersion: 'latest',
			sourceType: 'module',
			parserOptions: {
				ecmaFeatures: {
					jsx: true,
				},
				project: './tsconfig.json',
			},
		},
		settings: {
			react: {
				version: 'detect',
			},
		},
		plugins: {
			react: eslintPluginReact,
			'@typescript-eslint': eslintTypescriptPlugin,
			prettier: eslintPluginPrettier,
			import: eslintPluginImport,
		},
		rules: {
			// TypeScript
			'@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],

			// React
			'react/jsx-uses-react': 'off',
			'react/react-in-jsx-scope': 'off',

			// imports
			'import/order': [
				'warn',
				{
					groups: ['builtin', 'external', 'internal', 'parent', 'sibling', 'index'],
					'newlines-between': 'always',
				},
			],

			// Prettier
			'prettier/prettier': ['warn', { endOfLine: 'auto' }],

			// General
			'no-var': 'error',
			'prefer-const': 'warn',
      'no-console': ['warn', { allow: ['error'] }],
			'no-debugger': 'error',
			'no-unused-vars': 'off',
		},
	},
	{
		files: ['*.ts', '*.tsx'],
		rules: {
			'no-undef': 'off',
		},
	},
]
