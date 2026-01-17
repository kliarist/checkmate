FROM oven/bun:1-alpine AS builder
WORKDIR /app
COPY package.json bun.lockb ./
RUN bun install --frozen-lockfile
COPY . .
RUN bun run build

FROM oven/bun:1-alpine
WORKDIR /app
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/package.json ./
EXPOSE 5173
CMD ["bun", "run", "preview", "--host", "0.0.0.0"]

