module.exports = {
    'moduleFileExtensions': [
        'js',
        'jsx',
        'json',
        'vue',
        'ts',
        'tsx'
    ],
    'transform': {
        '^.+\\.vue$': 'vue-jest',
        '.+\\.(css|styl|less|sass|scss|svg|png|jpg|ttf|woff|woff2)$': 'jest-transform-stub',
        // JS 走 babel-jest，保证与 Vue CLI 3 的默认 Jest 处理链一致。
        '^.+\\.jsx?$': 'babel-jest',
        '^.+\\.tsx?$': 'ts-jest'
    },
    'transformIgnorePatterns': [
        '/node_modules/'
    ],
    'moduleNameMapper': {
        '^@/(.*)$': '<rootDir>/src/$1'
    },
    'snapshotSerializers': [
        'jest-serializer-vue'
    ],
    'testMatch': [
        '**/tests/unit/**/*.spec.(js|jsx|ts|tsx)|**/__tests__/*.(js|jsx|ts|tsx)'
    ],
    'collectCoverage': true,
    'collectCoverageFrom': [
        'src/utils/**/*.{ts,vue}',
        '!src/utils/auth.ts',
        '!src/utils/request.ts',
        'src/components/**/*.{ts,vue}'
    ],
    'coverageDirectory': '<rootDir>/tests/unit/coverage',
    'coverageReporters': [
        'lcov',
        'text-summary'
    ],
    'testURL': 'http://localhost/',
    'watchPlugins': [
        'jest-watch-typeahead/filename',
        'jest-watch-typeahead/testname'
    ],
    'globals': {
        'ts-jest': {
            // 当前仓库 Jest/Babel 版本链不一致，先关闭 ts-jest 的 Babel 二次处理，
            // 避免测试阶段再次触发 @vue/babel-preset-app 的运行时解析异常。
            'babelConfig': false
        }
    }
};
