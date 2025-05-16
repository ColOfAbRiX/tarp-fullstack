# #  Build Backend  # #

FROM eclipse-temurin:24-jre AS backend_builder
WORKDIR /build/backend

# SBT install via Coursier: https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html
RUN wget --output-document=coursier https://github.com/coursier/launchers/raw/master/coursier \
    && chmod +x coursier \
    && ./coursier setup -y

COPY backend/project /build/backend/project
COPY backend/build.sbt /build/backend/
RUN /root/.local/share/coursier/bin/sbt update

COPY backend /build/backend
RUN mkdir dist
RUN /root/.local/share/coursier/bin/sbt package
RUN find target -name "*.jar" -type f -not -path "*/libraries/*" -exec cp {} /build/backend/dist \;
RUN /root/.local/share/coursier/bin/sbt "export Runtime / fullClasspathAsJars" | tail -n 1 | tr ":" "\n" | xargs -I '{}' cp '{}' /build/backend/dist

# #  Build Frontend  # #

FROM node:23-slim AS frontend_builder
WORKDIR /build/frontend

COPY frontend/package.json frontend/package-lock.json /build/frontend/
RUN npm install

COPY frontend /build/frontend
RUN npm run build

# #  Start  # #

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app/backend

COPY --from=backend_builder /build/backend/dist /app/backend/dist
COPY --from=frontend_builder /build/frontend/dist /app/frontend/dist

ENV TARP_DEV_MODE="false"
EXPOSE 8080
ENTRYPOINT [ "java", "-cp", "/app/backend/dist/*", "com.colofabrix.scala.tarp.Main" ]
