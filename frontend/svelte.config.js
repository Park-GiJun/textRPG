import adapter from '@sveltejs/adapter-auto';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	kit: {
		adapter: adapter()
	},
	compilerOptions: {
		dev: true
	},
	preprocess: null,
	onwarn: (warning, handler) => {
		// Suppress TypeScript warnings
		if (warning.code === 'a11y-missing-attribute') return;
		if (warning.code === 'a11y-click-events-have-key-events') return;
		handler(warning);
	}
};

export default config;
