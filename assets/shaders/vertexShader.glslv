#version 150

uniform mat4 viewMatrix, projMatrix;

in vec3 position;
in vec3 color;
in vec2 texCoord;

out vec3 colorPS;
out vec2 texCoordPS;
out vec3 posPS;

void main() {
	vec4 pos = vec4(position.xyz, 1.0);


	posPS = position;
	colorPS = color;
	texCoordPS = texCoord;
	gl_Position =   projMatrix * viewMatrix * pos;

}

